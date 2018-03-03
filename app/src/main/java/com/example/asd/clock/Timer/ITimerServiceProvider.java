package com.example.asd.clock.Timer;

import org.dom4j.DocumentException;

import java.io.IOException;

interface ITimerServiceProvider {
    void pauseTimerRing();//稍后停止
    void close() throws IOException, DocumentException;//关闭
}
