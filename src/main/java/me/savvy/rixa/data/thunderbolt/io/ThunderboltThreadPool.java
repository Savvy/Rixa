package me.savvy.rixa.data.thunderbolt.io;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThunderboltThreadPool {
	
    @Getter
	private static ExecutorService pool = Executors.newCachedThreadPool();
	
	private ThunderboltThreadPool(){}
	
}
