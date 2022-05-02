package com.persoff68.fatodo.config.aop.cache;

import com.persoff68.fatodo.config.aop.cache.annotation.CacheEvictMethod;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheableMethod;
import com.persoff68.fatodo.config.aop.cache.annotation.MultiCacheEvictMethod;
import com.persoff68.fatodo.config.aop.cache.util.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheAspect {

    private final CacheManager cacheManager;

    @Around("@annotation(cacheableMethod)")
    public Object doCacheable(ProceedingJoinPoint pjp, CacheableMethod cacheableMethod) throws Throwable {
        return cacheable(pjp, cacheableMethod);
    }

    @Before("@annotation(cacheEvictMethod)")
    public void doCacheEvict(JoinPoint jp, CacheEvictMethod cacheEvictMethod) {
        cacheEvict(jp, cacheEvictMethod);
    }

    @Before("@annotation(multiCacheEvictMethod)")
    public void doCacheEvict(JoinPoint jp, MultiCacheEvictMethod multiCacheEvictMethod) {
        for (CacheEvictMethod cacheEvictMethod : multiCacheEvictMethod.value()) {
            cacheEvict(jp, cacheEvictMethod);
        }
    }

    private Object cacheable(ProceedingJoinPoint pjp, CacheableMethod cacheableMethod) throws Throwable {
        return cacheableMethod.keyCacheName().isEmpty()
                ? defaultCacheable(pjp, cacheableMethod)
                : listCacheable(pjp, cacheableMethod);
    }

    private void cacheEvict(JoinPoint jp, CacheEvictMethod cacheEvictMethod) {
        if (cacheEvictMethod.keyCacheName().isEmpty()) {
            defaultCacheEvict(jp, cacheEvictMethod);
        } else {
            listCacheEvict(jp, cacheEvictMethod);
        }
    }

    private Object defaultCacheable(ProceedingJoinPoint pjp, CacheableMethod cacheableMethod) throws Throwable {
        Cache cache = cacheManager.getCache(cacheableMethod.cacheName());
        Object key = getKey(pjp, cacheableMethod.key());
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null) {
                Object object = valueWrapper.get();
                log.debug("Read from cache: {} - {}", cacheableMethod.cacheName(), cacheableMethod.key());
                return getReturnType(pjp).isAssignableFrom(Optional.class)
                        ? Optional.ofNullable(object)
                        : object;
            }
        }
        Object result = pjp.proceed();
        if (cache != null) {
            log.debug("Write to cache: {} - {}", cacheableMethod.cacheName(), cacheableMethod.key());
            cache.put(key, result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object listCacheable(ProceedingJoinPoint pjp, CacheableMethod cacheableMethod) throws Throwable {
        Cache cache = cacheManager.getCache(cacheableMethod.cacheName());

        List<?> sortedList = getKeyCollection(pjp, cacheableMethod.key())
                .stream().sorted().toList();
        int hash = sortedList.hashCode();

        if (cache != null) {
            Object object = cache.get(hash, getReturnType(pjp));
            if (object != null) {
                log.debug("Read from cache: {} - {}", cacheableMethod.cacheName(), hash);
                return object;
            }
        }

        Cache keyCache = cacheManager.getCache(cacheableMethod.keyCacheName());
        Object result = pjp.proceed();

        if (cache != null && keyCache != null) {
            log.debug("Write to cache: {} - {}", cacheableMethod.cacheName(), hash);
            cache.put(hash, result);

            sortedList.forEach(key -> {
                List<Integer> keyHashList = keyCache.get(key, ArrayList.class);
                if (keyHashList == null) {
                    keyHashList = new ArrayList<>();
                }
                keyHashList.add(hash);
                log.debug("Write to key cache: {} - {}", cacheableMethod.keyCacheName(), key);
                keyCache.put(key, keyHashList);
            });
        }

        return result;
    }

    private void defaultCacheEvict(JoinPoint jp, CacheEvictMethod cacheEvictMethod) {
        Cache cache = cacheManager.getCache(cacheEvictMethod.cacheName());
        if (cache != null) {
            Collection<?> keyCollection = getKeyCollection(jp, cacheEvictMethod.key());
            for (Object key : keyCollection) {
                log.debug("Delete from cache: {} - {}", cacheEvictMethod.cacheName(), key);
                cache.evict(key);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void listCacheEvict(JoinPoint jp, CacheEvictMethod cacheEvictMethod) {
        Cache cache = cacheManager.getCache(cacheEvictMethod.cacheName());
        Cache keyCache = cacheManager.getCache(cacheEvictMethod.keyCacheName());
        if (cache != null && keyCache != null) {
            Collection<?> keyCollection = getKeyCollection(jp, cacheEvictMethod.key());
            for (Object key : keyCollection) {
                List<Integer> keyHashList = keyCache.get(key, ArrayList.class);
                if (keyHashList != null) {
                    keyHashList.forEach(hash -> {
                        log.debug("Delete from cache: {} - {}", cacheEvictMethod.cacheName(), hash);
                        cache.evict(hash);
                    });
                    log.debug("Delete from key cache: {} - {}", cacheEvictMethod.keyCacheName(), key);
                    keyCache.evict(key);
                }
            }
        }
    }

    private static Class<?> getReturnType(JoinPoint jp) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        return methodSignature.getReturnType();
    }

    private static Object getKey(JoinPoint jp, String key) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        String[] names = methodSignature.getParameterNames();
        Object[] args = jp.getArgs();
        return CacheUtils.getValue(names, args, key);
    }

    private static Collection<?> getKeyCollection(JoinPoint jp, String key) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        String[] names = methodSignature.getParameterNames();
        Object[] args = jp.getArgs();
        return CacheUtils.getCollectionValue(names, args, key);
    }

}
