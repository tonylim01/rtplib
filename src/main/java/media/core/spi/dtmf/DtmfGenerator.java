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

package media.core.spi.dtmf;

import media.core.spi.MediaSource;

/**
 * 
 * @author amit bhayani
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 * @author Yulian Oifa
 *
 */
public interface DtmfGenerator extends MediaSource 
{	
	public void addListener(DtmfGeneratorListener listener);
    public void removeListener(DtmfGeneratorListener listener);
    public void clearAllListeners();
     
	public void setDigit(String digit);
	public void setOOBDigit(String digit);
	public String getDigit();
	public String getOOBDigit();
	
	public void setToneDuration(int duration);
	public int getToneDuration();
	
	public void setVolume(int volume);
	public int getVolume();
	/**
     * Starts media processing.
     */
    public void start();
    
    /**
     * Terminates media processing.
     */
    public void stop();
}
