/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * @author antoinev
 */
public class CollectionDeletedEvent extends GwtEvent<CollectionDeletedHandler> {

    public static final Type<CollectionDeletedHandler> TYPE =
            new GwtEvent.Type<CollectionDeletedHandler>();
    
    private final String collectionUri;
    
    public CollectionDeletedEvent(String collectionUri) {
        this.collectionUri = collectionUri;
    }
    
    public String getCollectionUri() {
        return collectionUri;
    }
    
    @Override
    public Type<CollectionDeletedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CollectionDeletedHandler handler) {
        handler.onDeleteCollection(this);
    }
}