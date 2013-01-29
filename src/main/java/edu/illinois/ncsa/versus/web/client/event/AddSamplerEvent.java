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

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

/**
 *
 * @author antoinev
 */
public class AddSamplerEvent extends GwtEvent<AddSamplerEventHandler> {

    public static final Type<AddSamplerEventHandler> TYPE =
            new GwtEvent.Type<AddSamplerEventHandler>();

    private final ComponentMetadata samplerMetadata;

    public AddSamplerEvent(ComponentMetadata samplerMetada) {
        this.samplerMetadata = samplerMetada;
    }

    @Override
    public Type<AddSamplerEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AddSamplerEventHandler handler) {
        handler.onAddSampler(this);
    }

    public ComponentMetadata getSamplerMetadata() {
        return samplerMetadata;
    }
}
