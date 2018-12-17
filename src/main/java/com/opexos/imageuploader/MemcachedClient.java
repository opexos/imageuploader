package com.opexos.imageuploader;

import lombok.SneakyThrows;
import lombok.val;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides methods for working with Memcached server
 */
public class MemcachedClient {

    private final net.rubyeye.xmemcached.MemcachedClient memcachedClient;
    private static final Logger log = LoggerFactory.getLogger(MemcachedClient.class);

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
    public <T> T get(String key) {
        try {
            return memcachedClient.get(key);
        } catch (Exception e) {
            log.warn("Error occurred when get data from memcached", e);
            return null;
        }
    }

    /**
     * Store key-value item to memcached
     *
     * @param key        Stored key
     * @param expiration An expiration time, in seconds. Can be up to 30 days. After 30 days, is treated as a unix timestamp of an exact date.
     * @param value      Stored data
     */
    public void set(String key, int expiration, Object value) {
        try {
            memcachedClient.set(key, expiration, value);
        } catch (Exception e) {
            log.warn("Error occurred when set data to memcached", e);
        }
    }
}
