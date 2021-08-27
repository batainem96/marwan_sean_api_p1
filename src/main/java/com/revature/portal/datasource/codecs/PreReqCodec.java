package com.revature.portal.datasource.codecs;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.revature.portal.datasource.converters.PreReqConverter;
import com.revature.portal.datasource.models.PreReq;
import com.revature.portal.datasource.util.CodecFactory;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class PreReqCodec implements CollectibleCodec<PreReq> {

    private final CodecRegistry registry;
    private final Codec<Document> documentCodec;
    private final PreReqConverter converter;

    public PreReqCodec() {
        this.registry = MongoClientSettings.getDefaultCodecRegistry();
        this.documentCodec = this.registry.get(Document.class);
        this.converter = new PreReqConverter();
    }

    public PreReqCodec(Codec<Document> documentCodec) {
        this.registry = MongoClientSettings.getDefaultCodecRegistry();
        this.documentCodec = documentCodec;
        this.converter = new PreReqConverter();
    }

    public PreReqCodec(CodecRegistry registry) {
        this.registry = registry;
        this.documentCodec = this.registry.get(Document.class);
        this.converter = new PreReqConverter();
    }

    @Override
    public PreReq generateIdIfAbsentFromDocument(PreReq preReq) {
        return null;
    }

    @Override
    public boolean documentHasId(PreReq preReq) {
        return false;
    }

    @Override
    public BsonValue getDocumentId(PreReq preReq) {
        return null;
    }

    @Override
    public PreReq decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);
        PreReq preReq = this.converter.convert(document);

        return preReq;
    }

    @Override
    public void encode(BsonWriter bsonWriter, PreReq preReq, EncoderContext encoderContext) {
        Document document = this.converter.convert(preReq);
        documentCodec.encode(bsonWriter, document, encoderContext);
    }

    @Override
    public Class<PreReq> getEncoderClass() {
        return PreReq.class;
    }
}
