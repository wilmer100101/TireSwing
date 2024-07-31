package se.wilmer.tireswing.configuration;

import io.leangen.geantyref.GenericTypeReflector;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class Vector3fcSerializer implements TypeSerializer<Vector3fc> {

    @Override
    public Vector3fc deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!Vector3fc.class.isAssignableFrom(GenericTypeReflector.erase(type))) {
            throw new SerializationException(type, "Expected type Vector3fc");
        }
        float x = node.node("x").getFloat();
        float y = node.node("y").getFloat();
        float z = node.node("z").getFloat();
        return new Vector3f(x, y, z);
    }

    @Override
    public void serialize(Type type, @Nullable Vector3fc obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            return;
        }
        if (!Vector3fc.class.isAssignableFrom(GenericTypeReflector.erase(type))) {
            throw new SerializationException(type, "Expected type Vector3fc");
        }
        node.node("x").set(obj.x());
        node.node("y").set(obj.y());
        node.node("z").set(obj.z());
    }
}
