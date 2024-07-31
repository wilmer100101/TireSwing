package se.wilmer.tireswing.configuration;

import io.leangen.geantyref.GenericTypeReflector;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class QuaternionfcSerializer implements TypeSerializer<Quaternionfc> {

    @Override
    public Quaternionfc deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!Quaternionfc.class.isAssignableFrom(GenericTypeReflector.erase(type))) {
            throw new SerializationException(type, "Expected type Quaternionfc");
        }
        float x = node.node("x").getFloat();
        float y = node.node("y").getFloat();
        float z = node.node("z").getFloat();
        float w = node.node("w").getFloat();
        return new Quaternionf(x, y, z, w);
    }

    @Override
    public void serialize(Type type, @Nullable Quaternionfc obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            return;
        }
        if (!Quaternionfc.class.isAssignableFrom(GenericTypeReflector.erase(type))) {
            throw new SerializationException(type, "Expected type Quaternionfc");
        }
        node.node("x").set(obj.x());
        node.node("y").set(obj.y());
        node.node("z").set(obj.z());
        node.node("w").set(obj.w());
    }
}
