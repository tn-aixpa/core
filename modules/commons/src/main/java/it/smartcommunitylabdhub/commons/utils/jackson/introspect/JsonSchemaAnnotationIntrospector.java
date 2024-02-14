package it.smartcommunitylabdhub.commons.utils.jackson.introspect;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import it.smartcommunitylabdhub.commons.annotations.schema.JsonSchemaIgnore;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.util.Assert;

public class JsonSchemaAnnotationIntrospector extends JacksonAnnotationIntrospector {

    private final Set<Class<?>> classes;

    public JsonSchemaAnnotationIntrospector() {
        this.classes = null;
    }

    public JsonSchemaAnnotationIntrospector(Collection<Class<?>> classes) {
        Assert.notNull(classes, "please provide a valid class list");
        this.classes = Collections.unmodifiableSet(new HashSet<>(classes));
    }

    public JsonSchemaAnnotationIntrospector(Class<?>... classes) {
        this(Arrays.asList(classes));
    }

    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        // first check annotation
        JsonSchemaIgnore ann = _findAnnotation(m, JsonSchemaIgnore.class);
        if (ann != null) {
            return ann.value();
        }

        // check class match
        if (classes != null && classes.contains(m.getDeclaringClass())) {
            return true;
        }

        // delegate
        return super.hasIgnoreMarker(m);
    }
}
