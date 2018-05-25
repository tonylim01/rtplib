/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
 * by the @authors tag. 
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

package media.core.dtmf;

import media.core.spi.listener.Listener;

/**
 * Listener that is notified about events raised by {@link DtmfDetector}.
 * 
 * @author Vladimir Morosev (vladimir.morosev@telestax.com)
 *
 */
public interface DtmfDetectorListener extends Listener {

    /**
     * Event raised when DTMF signal is detected.
     *
     * @param tone Detected tone
     */
    void onDtmfDetected(String tone);

}
