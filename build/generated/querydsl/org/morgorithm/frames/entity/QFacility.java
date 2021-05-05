package org.morgorithm.frames.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFacility is a Querydsl query type for Facility
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFacility extends EntityPathBase<Facility> {

    private static final long serialVersionUID = 3186464L;

    public static final QFacility facility = new QFacility("facility");

    public final NumberPath<Long> bno = createNumber("bno", Long.class);

    public final StringPath building = createString("building");

    public final ListPath<Device, QDevice> devices = this.<Device, QDevice>createList("devices", Device.class, QDevice.class, PathInits.DIRECT2);

    public QFacility(String variable) {
        super(Facility.class, forVariable(variable));
    }

    public QFacility(Path<? extends Facility> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFacility(PathMetadata metadata) {
        super(Facility.class, metadata);
    }

}

