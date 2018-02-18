package com.example.asd.clock.Clock;

import org.dom4j.DocumentException;

import java.io.IOException;

interface IAlarmClockServiceProvider {
    void pause9MinRing();
    void nineMinClose();
    void close() throws IOException, DocumentException;
}
