package com.revature.portal.datasource.util;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.revature.portal.datasource.codecs.PreReqCodec;
import com.revature.portal.datasource.models.PreReq;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class CodecFactory {

    private CodecRegistry codecRegistry;
    private static final CodecFactory codecFactory = new CodecFactory();

    private CodecFactory() {
        CodecRegistry codecRegistry = MongoClientSettings.getDefaultCodecRegistry();
        Codec<Document> documentCodec = codecRegistry.get(Document.class);
        Codec<PreReq> preReqCodec = new PreReqCodec(codecRegistry);

        codecRegistry = CodecRegistries.fromRegistries(
                                        MongoClientSettings.getDefaultCodecRegistry(),
                                        CodecRegistries.fromCodecs(
                                                documentCodec,
                                                preReqCodec
                                        )
                                        );

        this.codecRegistry = codecRegistry;
    }

    public static CodecFactory getInstance(){
        return codecFactory;
    }

    public CodecRegistry getRegistry() {return codecRegistry;}
}
