package com.inmaytide.orbit.uaa;

import com.inmaytide.orbit.commons.business.id.snowflake.SnowflakeIdGenerator;
import com.inmaytide.orbit.commons.business.id.snowflake.SnowflakeIdWorker;

/**
 * @author inmaytide
 * @since 2024/4/29
 */
public class IdGeneratorTester {

    public static void main(String[] args) {
        SnowflakeIdWorker idGenerator = new SnowflakeIdWorker(1, 1);
        for (int i = 0; i < 100; i++) {
            System.out.println(idGenerator.nextId());
        }

    }

}
