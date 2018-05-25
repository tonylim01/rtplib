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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package media.core.test.java.media.core.rtp;

import media.core.component.audio.AudioComponent;
import media.core.component.audio.AudioMixer;
import media.core.component.audio.Sine;
import media.core.component.audio.SpectraAnalyzer;
import media.core.component.dsp.Dsp;
import media.core.component.dsp.DspFactoryImpl;
import media.core.network.deprecated.RtpPortManager;
import media.core.network.deprecated.UdpManager;
import media.core.rtp.rtp.ChannelsManager;
import media.core.rtp.rtp.RTPDataChannel;
import media.core.rtp.rtp.crypto.DtlsSrtpServer;
import media.core.rtp.rtp.crypto.DtlsSrtpServerProvider;
import media.core.scheduler.Clock;
import media.core.scheduler.PriorityQueueScheduler;
import media.core.scheduler.Scheduler;
import media.core.scheduler.ServiceScheduler;
import media.core.scheduler.WallClock;
import media.core.sdp.format.AVProfile;
import media.core.spi.ConnectionMode;
import media.core.spi.format.AudioFormat;
import media.core.spi.format.FormatFactory;
import media.core.spi.format.Formats;

import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author oifa yulian
 */
@Deprecated
@Ignore
public class RTPDataChannelTest {

    //clock and scheduler
    private Clock clock;
    private PriorityQueueScheduler mediaScheduler;
    private final Scheduler scheduler;

    private ChannelsManager channelsManager;
    private UdpManager udpManager;

    private SpectraAnalyzer analyzer1, analyzer2;
    private Sine source1, source2;

    private RTPDataChannel channel1, channel2;
    
    private int fcount;

    private DspFactoryImpl dspFactory = new DspFactoryImpl();
    
    private Dsp dsp11, dsp12;
    private Dsp dsp21, dsp22;
    
    private AudioMixer audioMixer1,audioMixer2;
    private AudioComponent component1,component2;
    
    private static final int cipherSuites[] = { 0xc030, 0xc02f, 0xc028, 0xc027, 0xc014, 0xc013, 0x009f, 0x009e, 0x006b, 0x0067,
            0x0039, 0x0033, 0x009d, 0x009c, 0x003d, 0x003c, 0x0035, 0x002f, 0xc02b };
    
    public RTPDataChannelTest() {
        scheduler = new ServiceScheduler();
    }

    @Before
    public void setUp() throws Exception {
        // given
        DtlsSrtpServerProvider mockedDtlsServerProvider = mock(DtlsSrtpServerProvider.class);
        DtlsSrtpServer mockedDtlsSrtpServer = mock(DtlsSrtpServer.class);
        
        // when
        when(mockedDtlsServerProvider.provide()).thenReturn(mockedDtlsSrtpServer);
        when(mockedDtlsSrtpServer.getCipherSuites()).thenReturn(cipherSuites);
        
        // then
    	AudioFormat pcma = FormatFactory.createAudioFormat("pcma", 8000, 8, 1);
        Formats fmts = new Formats();
        fmts.add(pcma);
        
        Formats dstFormats = new Formats();
        dstFormats.add(FormatFactory.createAudioFormat("LINEAR", 8000, 16, 1));
        
        dspFactory.addCodec("org.mobicents.media.server.impl.dsp.audio.g711.alaw.Encoder");
        dspFactory.addCodec("org.mobicents.media.server.impl.dsp.audio.g711.alaw.Decoder");

        dsp11 = dspFactory.newProcessor();
        dsp12 = dspFactory.newProcessor();

        dsp21 = dspFactory.newProcessor();
        dsp22 = dspFactory.newProcessor();
        
        //use default clock
        clock = new WallClock();

        //create single thread scheduler
        mediaScheduler = new PriorityQueueScheduler();
        mediaScheduler.setClock(clock);
        mediaScheduler.start();

        udpManager = new UdpManager(scheduler, new RtpPortManager(), new RtpPortManager());
        scheduler.start();
        udpManager.start();
        
        channelsManager = new ChannelsManager(udpManager, mockedDtlsServerProvider);
        channelsManager.setScheduler(mediaScheduler);

        source1 = new Sine(mediaScheduler);
        source1.setFrequency(100);        
        
        source2 = new Sine(mediaScheduler);
        source2.setFrequency(50);
        
        analyzer1 = new SpectraAnalyzer("analyzer",mediaScheduler);        
        analyzer2 = new SpectraAnalyzer("analyzer",mediaScheduler);
        
        channel1 = channelsManager.getChannel();
        channel1.updateMode(ConnectionMode.SEND_RECV);
        channel1.setOutputDsp(dsp11);
        channel1.setOutputFormats(fmts);        
        channel1.setInputDsp(dsp12);
        
        channel2 = channelsManager.getChannel();
        channel2.updateMode(ConnectionMode.SEND_RECV);
        channel2.setOutputDsp(dsp21);
        channel2.setOutputFormats(fmts);
        channel2.setInputDsp(dsp22);        
        
        channel1.bind(false);
        channel2.bind(false);

        channel1.setPeer(new InetSocketAddress("127.0.0.1", channel2.getLocalPort()));
        channel2.setPeer(new InetSocketAddress("127.0.0.1", channel1.getLocalPort()));

        channel1.setFormatMap(AVProfile.audio);
        channel2.setFormatMap(AVProfile.audio);

        audioMixer1=new AudioMixer(mediaScheduler);
        audioMixer2=new AudioMixer(mediaScheduler);
        
        component1=new AudioComponent(1);
        component1.addInput(source1.getAudioInput());
        component1.addOutput(analyzer1.getAudioOutput());
        component1.updateMode(true,true);
        
        audioMixer1.addComponent(component1);
        audioMixer1.addComponent(channel1.getAudioComponent());
        
        component2=new AudioComponent(2);
        component2.addInput(source2.getAudioInput());
        component2.addOutput(analyzer2.getAudioOutput());
        component2.updateMode(true,true);
        
        audioMixer2.addComponent(component2);
        audioMixer2.addComponent(channel2.getAudioComponent());           
    }

    @After
    public void tearDown() {
    	source1.deactivate();
    	channel1.close();

    	source2.deactivate();
    	channel2.close();

    	audioMixer1.stop();
    	audioMixer2.stop();
    	
        udpManager.stop();
        mediaScheduler.stop();
        scheduler.stop();
    }

    @Test
    public void testTransmission() throws Exception {
    	source1.activate();
    	analyzer1.activate();
    	audioMixer1.start();

    	source2.start();
    	analyzer2.activate();
    	audioMixer2.start();
        
        Thread.sleep(5000);
        
        analyzer1.deactivate();
        analyzer2.deactivate();
        source1.deactivate();
        source2.deactivate();
        audioMixer1.stop();        
        audioMixer2.stop();
        
//        Thread.sleep(5000);

        int s1[] = analyzer1.getSpectra();
        int s2[] = analyzer2.getSpectra();

//        print(s1);
//        print(s2);

        System.out.println("rx-channel1: " + channel1.getPacketsReceived());
        System.out.println("tx-channel1: " + channel1.getPacketsTransmitted());

        System.out.println("rx-channel2: " + channel2.getPacketsReceived());
        System.out.println("tx-channel2: " + channel2.getPacketsTransmitted());

        if (s1.length != 1 || s2.length != 1) {
            System.out.println("Failure ,s1:" + s1.length + ",s2:" + s2.length);
            fcount++;
        } else System.out.println("Passed");
        
        assertEquals(1, s1.length);
        assertEquals(1, s2.length);
        assertEquals(50, s1[0], 5);
        assertEquals(100, s2[0], 5);
    }

    @Test
    public void testHalfDuplex() throws Exception {
    	channel1.updateMode(ConnectionMode.RECV_ONLY);    	
    	channel2.updateMode(ConnectionMode.SEND_ONLY);
    	source1.activate();
    	source2.activate();
    	analyzer1.activate();
    	audioMixer1.start();
    	audioMixer2.start();
        
        Thread.sleep(5000);
        
        source1.deactivate();
        source2.deactivate();
        analyzer1.deactivate();
        audioMixer1.stop();
        audioMixer2.stop();
        
        int s1[] = analyzer1.getSpectra();
        int s2[] = analyzer2.getSpectra();

        System.out.println("rx-channel1: " + channel1.getPacketsReceived());
        System.out.println("tx-channel1: " + channel1.getPacketsTransmitted());

        System.out.println("rx-channel2: " + channel2.getPacketsReceived());
        System.out.println("tx-channel2: " + channel2.getPacketsTransmitted());

        if (s2.length != 0 || s1.length != 1) {
        	fcount++;
        } else System.out.println("Passed");
        
        assertEquals(0, fcount);
        assertEquals(50, s1[0], 5);
    }
    
    @Test
    public void testFailureRate() throws Exception {
        for (int i = 0; i < 1; i++) {
            System.out.println("Test# " + i);
            this.testTransmission();
        }
        assertEquals(0, fcount);
    }

//    private void print(int[] s) {
//        for (int i = 0; i < s.length; i++) {
//            System.out.print(s[i] + " ");
//        }
//        System.out.println();
//    }
}