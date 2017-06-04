package me.savvy.rixa.data.thunderbolt.io;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThunderboltThreadPool {
	
	private static ExecutorService pool = Executors.newCachedThreadPool();
	
	private ThunderboltThreadPool(){}
	
	public static ExecutorService getPool(){
		return pool;
	}	
}
