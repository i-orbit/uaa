package com.inmaytide.orbit.uaa.configuration.oauth2.store;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author inmaytide
 * @since 2024/1/25
 */
public interface Store<T>{

    void store(T t);

    void remove(T t);

    Optional<T> get(String id);

    List<T> all();

}
