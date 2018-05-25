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

package media.core.g711.alaw;

import media.core.spi.dsp.Codec;
import media.core.spi.format.Format;
import media.core.spi.format.FormatFactory;
import media.core.spi.memory.Frame;
import media.core.spi.memory.Memory;

/**
 * Implements G.711 A-Law decompressor.
 * 
 * @author Oleg Kulikov
 */
public class Decoder implements Codec {

    private final static Format alaw = FormatFactory.createAudioFormat("pcma", 8000, 8, 1);
    private final static Format linear = FormatFactory.createAudioFormat("linear", 8000, 16, 1);

    private int j=0,i=0;
    private int sourceLen=0,destinationLen=0;
    private int currentIndex;
    
    /** decompress table constants separated into low and high bytes*/
    private static byte aLawDecompressTable_low[] = new byte[]{
    	(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,
    	(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,
    	(byte)0x40,(byte)0xc0,(byte)0x40,(byte)0xc0,(byte)0x40,(byte)0xc0,(byte)0x40,(byte)0xc0,
    	(byte)0x40,(byte)0xc0,(byte)0x40,(byte)0xc0,(byte)0x40,(byte)0xc0,(byte)0x40,(byte)0xc0,
    	(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
    	(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
    	(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
    	(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
    	(byte)0xa8,(byte)0xb8,(byte)0x88,(byte)0x98,(byte)0xe8,(byte)0xf8,(byte)0xc8,(byte)0xd8,
    	(byte)0x28,(byte)0x38,(byte)0x8,(byte)0x18,(byte)0x68,(byte)0x78,(byte)0x48,(byte)0x58,
    	(byte)0xa8,(byte)0xb8,(byte)0x88,(byte)0x98,(byte)0xe8,(byte)0xf8,(byte)0xc8,(byte)0xd8,
    	(byte)0x28,(byte)0x38,(byte)0x8,(byte)0x18,(byte)0x68,(byte)0x78,(byte)0x48,(byte)0x58,
    	(byte)0xa0,(byte)0xe0,(byte)0x20,(byte)0x60,(byte)0xa0,(byte)0xe0,(byte)0x20,(byte)0x60,
    	(byte)0xa0,(byte)0xe0,(byte)0x20,(byte)0x60,(byte)0xa0,(byte)0xe0,(byte)0x20,(byte)0x60,
    	(byte)0x50,(byte)0x70,(byte)0x10,(byte)0x30,(byte)0xd0,(byte)0xf0,(byte)0x90,(byte)0xb0,
    	(byte)0x50,(byte)0x70,(byte)0x10,(byte)0x30,(byte)0xd0,(byte)0xf0,(byte)0x90,(byte)0xb0,
    	(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,
    	(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,
    	(byte)0xc0,(byte)0x40,(byte)0xc0,(byte)0x40,(byte)0xc0,(byte)0x40,(byte)0xc0,(byte)0x40,
    	(byte)0xc0,(byte)0x40,(byte)0xc0,(byte)0x40,(byte)0xc0,(byte)0x40,(byte)0xc0,(byte)0x40,
    	(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
    	(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
    	(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
    	(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
    	(byte)0x58,(byte)0x48,(byte)0x78,(byte)0x68,(byte)0x18,(byte)0x8,(byte)0x38,(byte)0x28,
    	(byte)0xd8,(byte)0xc8,(byte)0xf8,(byte)0xe8,(byte)0x98,(byte)0x88,(byte)0xb8,(byte)0xa8,
    	(byte)0x58,(byte)0x48,(byte)0x78,(byte)0x68,(byte)0x18,(byte)0x8,(byte)0x38,(byte)0x28,
    	(byte)0xd8,(byte)0xc8,(byte)0xf8,(byte)0xe8,(byte)0x98,(byte)0x88,(byte)0xb8,(byte)0xa8,
    	(byte)0x60,(byte)0x20,(byte)0xe0,(byte)0xa0,(byte)0x60,(byte)0x20,(byte)0xe0,(byte)0xa0,
    	(byte)0x60,(byte)0x20,(byte)0xe0,(byte)0xa0,(byte)0x60,(byte)0x20,(byte)0xe0,(byte)0xa0,
    	(byte)0xb0,(byte)0x90,(byte)0xf0,(byte)0xd0,(byte)0x30,(byte)0x10,(byte)0x70,(byte)0x50,
    	(byte)0xb0,(byte)0x90,(byte)0xf0,(byte)0xd0,(byte)0x30,(byte)0x10,(byte)0x70,(byte)0x50,
    };
    	
    private static byte aLawDecompressTable_high[] = new byte[]{
    	(byte)0xea,(byte)0xeb,(byte)0xe8,(byte)0xe9,(byte)0xee,(byte)0xef,(byte)0xec,(byte)0xed,
    	(byte)0xe2,(byte)0xe3,(byte)0xe0,(byte)0xe1,(byte)0xe6,(byte)0xe7,(byte)0xe4,(byte)0xe5,
    	(byte)0xf5,(byte)0xf5,(byte)0xf4,(byte)0xf4,(byte)0xf7,(byte)0xf7,(byte)0xf6,(byte)0xf6,
    	(byte)0xf1,(byte)0xf1,(byte)0xf0,(byte)0xf0,(byte)0xf3,(byte)0xf3,(byte)0xf2,(byte)0xf2,
    	(byte)0xaa,(byte)0xae,(byte)0xa2,(byte)0xa6,(byte)0xba,(byte)0xbe,(byte)0xb2,(byte)0xb6,
    	(byte)0x8a,(byte)0x8e,(byte)0x82,(byte)0x86,(byte)0x9a,(byte)0x9e,(byte)0x92,(byte)0x96,
    	(byte)0xd5,(byte)0xd7,(byte)0xd1,(byte)0xd3,(byte)0xdd,(byte)0xdf,(byte)0xd9,(byte)0xdb,
    	(byte)0xc5,(byte)0xc7,(byte)0xc1,(byte)0xc3,(byte)0xcd,(byte)0xcf,(byte)0xc9,(byte)0xcb,
    	(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,
    	(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,
    	(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
    	(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
    	(byte)0xfa,(byte)0xfa,(byte)0xfa,(byte)0xfa,(byte)0xfb,(byte)0xfb,(byte)0xfb,(byte)0xfb,
    	(byte)0xf8,(byte)0xf8,(byte)0xf8,(byte)0xf8,(byte)0xf9,(byte)0xf9,(byte)0xf9,(byte)0xf9,
    	(byte)0xfd,(byte)0xfd,(byte)0xfd,(byte)0xfd,(byte)0xfd,(byte)0xfd,(byte)0xfd,(byte)0xfd,
    	(byte)0xfc,(byte)0xfc,(byte)0xfc,(byte)0xfc,(byte)0xfc,(byte)0xfc,(byte)0xfc,(byte)0xfc,
    	(byte)0x15,(byte)0x14,(byte)0x17,(byte)0x16,(byte)0x11,(byte)0x10,(byte)0x13,(byte)0x12,
    	(byte)0x1d,(byte)0x1c,(byte)0x1f,(byte)0x1e,(byte)0x19,(byte)0x18,(byte)0x1b,(byte)0x1a,
    	(byte)0xa,(byte)0xa,(byte)0xb,(byte)0xb,(byte)0x8,(byte)0x8,(byte)0x9,(byte)0x9,
    	(byte)0xe,(byte)0xe,(byte)0xf,(byte)0xf,(byte)0xc,(byte)0xc,(byte)0xd,(byte)0xd,
    	(byte)0x56,(byte)0x52,(byte)0x5e,(byte)0x5a,(byte)0x46,(byte)0x42,(byte)0x4e,(byte)0x4a,
    	(byte)0x76,(byte)0x72,(byte)0x7e,(byte)0x7a,(byte)0x66,(byte)0x62,(byte)0x6e,(byte)0x6a,
    	(byte)0x2b,(byte)0x29,(byte)0x2f,(byte)0x2d,(byte)0x23,(byte)0x21,(byte)0x27,(byte)0x25,
    	(byte)0x3b,(byte)0x39,(byte)0x3f,(byte)0x3d,(byte)0x33,(byte)0x31,(byte)0x37,(byte)0x35,
    	(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,
    	(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,
    	(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
    	(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
    	(byte)0x5,(byte)0x5,(byte)0x5,(byte)0x5,(byte)0x4,(byte)0x4,(byte)0x4,(byte)0x4,
    	(byte)0x7,(byte)0x7,(byte)0x7,(byte)0x7,(byte)0x6,(byte)0x6,(byte)0x6,(byte)0x6,
    	(byte)0x2,(byte)0x2,(byte)0x2,(byte)0x2,(byte)0x2,(byte)0x2,(byte)0x2,(byte)0x2,
    	(byte)0x3,(byte)0x3,(byte)0x3,(byte)0x3,(byte)0x3,(byte)0x3,(byte)0x3,(byte)0x3,
    };


    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormat().
     */
    public Format getSupportedInputFormat() {
        return alaw;
    }

    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormat().
     */
    public Format getSupportedOutputFormat() {
        return linear;
    }

    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.dsp.Codec#process(Frame).
     */
    public Frame process(Frame frame) {
    	sourceLen=frame.getLength();
    	destinationLen=sourceLen * 2;
        Frame res = Memory.allocate(destinationLen);
        
        byte[] data=frame.getData();
        byte[] resData=res.getData();
        
        for (i = 0,j = 0; i < sourceLen; i++) 
        {
        	currentIndex = data[i] & 0xff;
            resData[j++] = aLawDecompressTable_low[currentIndex];
            resData[j++] = aLawDecompressTable_high[currentIndex];
        }
        
        res.setOffset(0);
        res.setLength(destinationLen);
        res.setTimestamp(frame.getTimestamp());
        res.setDuration(frame.getDuration());
        res.setSequenceNumber(frame.getSequenceNumber());
        res.setEOM(frame.isEOM());
        res.setFormat(linear);
        res.setHeader(frame.getHeader());
        return res;
    }
}