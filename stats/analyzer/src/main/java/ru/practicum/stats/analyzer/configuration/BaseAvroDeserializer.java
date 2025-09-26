package ru.practicum.stats.analyzer.configuration;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private static final Logger LOG = LoggerFactory.getLogger(BaseAvroDeserializer.class);

    private DecoderFactory decoderFactory = DecoderFactory.get();

    protected final Schema schema;

    public BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.schema = schema;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            T result = null;
            if (data != null) {
                DatumReader<T> reader = new SpecificDatumReader<T>(schema);
                result = reader.read(null, decoderFactory.binaryDecoder(data, null));
                return result;
            }
            return null;
        } catch (Exception e) {
            throw new SerializationException("Ошибка десереализации данных из топика [" + topic + "]", e);
        }
    }

}
