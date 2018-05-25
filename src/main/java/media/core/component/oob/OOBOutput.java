/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package media.core.component.oob;

import media.core.component.AbstractSink;
import media.core.component.AbstractSource;
import media.core.concurrent.ConcurrentCyclicFIFO;
import media.core.scheduler.PriorityQueueScheduler;
import media.core.spi.memory.Frame;

/**
 * Implements output for compound components.
 * 
 * @author Yulian Oifa
 */
public class OOBOutput extends AbstractSource {
	
	private static final long serialVersionUID = -1350715959623627363L;

	private int outputId;
    private ConcurrentCyclicFIFO<Frame> buffer = new ConcurrentCyclicFIFO<Frame>();
    
    /**
     * Creates new instance with default name.
     */
    public OOBOutput(PriorityQueueScheduler scheduler,int outputId) {
        super("compound.output", scheduler, PriorityQueueScheduler.OUTPUT_QUEUE);
        this.outputId=outputId;
    }

    public int getOutputId() {
    	return outputId;
    }
    
    public void join(AbstractSink sink) {
    	connect(sink);
    }
    
    public void unjoin() {
    	disconnect();
    }
    
    @Override
    public Frame evolve(long timestamp) {
    	return buffer.poll();
    }

    @Override
    public void stop() {
    	while(buffer.size()>0) {
    		buffer.poll().recycle();
    	}
    	super.stop();            
    }
    
    public void resetBuffer() {
    	while(buffer.size()>0) {
    		buffer.poll().recycle();
    	}
    }
    
    public void offer(Frame frame) {
    	if(buffer.size()>1) {
        	buffer.poll().recycle();
    	}
    	buffer.offer(frame);
    }

    @Override
    public void perform(Frame frame) {

    }
}
