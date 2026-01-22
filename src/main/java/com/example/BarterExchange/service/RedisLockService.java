package com.example.BarterExchange.service;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setScriptText(
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) " +
            "else return 0 end"
        );
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    private final StringRedisTemplate redisTemplate;

    public String tryLock(String key, Duration ttl) {
        String token = UUID.randomUUID().toString();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(key, token, ttl);
        return Boolean.TRUE.equals(locked) ? token : null;
    }

    public void unlock(String key, String token) {
        redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(key), token);
    }
}
