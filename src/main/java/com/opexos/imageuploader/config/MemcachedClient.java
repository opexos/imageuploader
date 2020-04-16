package com.opexos.imageuploader.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * Provides methods for working with Memcached server
 */
@Slf4j
public class MemcachedClient {

    private final net.rubyeye.xmemcached.MemcachedClient memcachedClient;

    /**
     * Creates a new instance
     *
     * @param address            Address of server in format  'host:port'
     * @param connectionPoolSize Connection pool size
     * @param timeout            Operation timeout value in milliseconds
     */
    @SneakyThrows
    public MemcachedClient(String address, int connectionPoolSize, long timeout) {
        val builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(address));
        builder.setConnectionPoolSize(connectionPoolSize);
        builder.setOpTimeout(timeout);
        builder.setCommandFactory(new BinaryCommandFactory());//use binary protocol
        memcachedClient = builder.build();
    }

    /**
     * Get value by key
     */
    @SneakyThrows
    public <T> T get(String key) {
        return memcachedClient.get(key);
    }

    /**
     * Store key-value item to memcached
     *
     * @param key        Stored key
     * @param expiration An expiration time, in seconds
     * @param value      Stored data
     */
    @SneakyThrows
    public void set(String key, int expiration, Object value) {
        memcachedClient.set(key, expiration, value);
    }
}
