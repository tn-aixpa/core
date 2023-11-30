package it.smartcommunitylabdhub.core.utils;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
public class BeanProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    // Retrieve a single bean by type
    public static <T> Optional<T> getBean(Class<T> type) {
        return Optional.of(applicationContext.getBean(type));
    }

    // Retrieve a single bean by name and type
    public static <T> Optional<T> getBean(String name, Class<T> type) {
        return Optional.of(applicationContext.getBean(name, type));
    }


    @SuppressWarnings("unchecked")
    public static <T, R extends Spec> Optional<SpecRegistry<R>> getSpecRegistryBean(Class<T> type) {
        return Optional.of(applicationContext.getBean(type))
                .map(bean -> (SpecRegistry<R>) bean);
    }

    @SuppressWarnings("unchecked")
    public static <T, R extends Accessor<Object>> Optional<AccessorRegistry<R>> getAccessorRegistryBean(Class<T> type) {
        return Optional.of(applicationContext.getBean(type))
                .map(bean -> (AccessorRegistry<R>) bean);
    }


    // Retrieve all beans of a certain type
    public static <T> List<T> getAllBeans(Class<T> type) {
        Map<String, T> beansOfType = applicationContext.getBeansOfType(type);
        return new ArrayList<>(beansOfType.values());
    }


    // Retrieve a lazily initialized bean
    public static <T> Optional<T> getLazyBean(Class<T> type) {
        return Optional.of(applicationContext.getAutowireCapableBeanFactory().createBean(type));
    }

    // Retrieve a prototype-scoped bean
    public static <T> Optional<T> getPrototypeBean(Class<T> type) {
        return Optional.of(applicationContext.getBean(type));
    }

    // Perform a custom action when the bean is present

    public static <T, R> Optional<R> ifBeanPresent(Class<T> type, Function<T, R> action) {
        return getBean(type).map(action);
    }

    // Perform a custom action when the bean is not present
    public static <T, R> Optional<R> ifBeanAbsent(Class<T> type, Function<T, R> action) {
        if (getBean(type).isEmpty()) {
            return getBean(type).map(action);
        }
        return Optional.empty();
    }

    // Retrieve a bean based on a condition
    public static <T> Optional<T> getBeanWithCondition(Class<T> type, Predicate<T> condition) {
        return getBean(type).filter(condition);
    }

    // Provide a callback function to initialize the retrieved bean
    public static <T> Optional<T> getBeanWithInitialization(Class<T> type, Consumer<T> initializationCallback) {
        return getBean(type).map(bean -> {
            initializationCallback.accept(bean);
            return bean;
        });
    }

    // Refresh a singleton-scoped bean
    public static <T> void refreshBean(Class<T> type) {
        String beanName = applicationContext.getBeanNamesForType(type)[0];
        if (applicationContext.isSingleton(beanName)) {
            Object bean = applicationContext.getBean(beanName);
            applicationContext.getAutowireCapableBeanFactory().destroyBean(bean);
        }
    }


    // Perform a custom action when the bean is present and initialize it
    public static <T> void ifBeanPresentInitialize(Class<T> type, Consumer<T> initializationCallback) {
        getBean(type).ifPresent(initializationCallback);
    }


    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

}
