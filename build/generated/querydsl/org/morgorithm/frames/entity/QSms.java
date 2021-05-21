package org.morgorithm.frames.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSms is a Querydsl query type for Sms
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSms extends EntityPathBase<Sms> {

    private static final long serialVersionUID = 1736995516L;

    public static final QSms sms = new QSms("sms");

    public final StringPath content = createString("content");

    public final StringPath receivedDate = createString("receivedDate");

    public final NumberPath<Long> rno = createNumber("rno", Long.class);

    public final StringPath sender = createString("sender");

    public QSms(String variable) {
        super(Sms.class, forVariable(variable));
    }

    public QSms(Path<? extends Sms> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSms(PathMetadata metadata) {
        super(Sms.class, metadata);
    }

}

