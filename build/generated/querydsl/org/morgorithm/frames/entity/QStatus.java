package org.morgorithm.frames.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStatus is a Querydsl query type for Status
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QStatus extends EntityPathBase<Status> {

    private static final long serialVersionUID = 1073478575L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStatus status = new QStatus("status");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QFacility facility;

    public final QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final BooleanPath state = createBoolean("state");

    public final NumberPath<Long> statusnum = createNumber("statusnum", Long.class);

    public final NumberPath<Double> temperature = createNumber("temperature", Double.class);

    public QStatus(String variable) {
        this(Status.class, forVariable(variable), INITS);
    }

    public QStatus(Path<? extends Status> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStatus(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStatus(PathMetadata metadata, PathInits inits) {
        this(Status.class, metadata, inits);
    }

    public QStatus(Class<? extends Status> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.facility = inits.isInitialized("facility") ? new QFacility(forProperty("facility")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

