package com.ag1.test1;

import java.io.FileNotFoundException;


import org.junit.Test;

import com.ec.polarion.agco.Agco;

public class AG1Test {
	
	@Test
	public void testAg1Main() throws FileNotFoundException,Exception {
	Agco a = new Agco();
		a.updateWorkItem("C:\\Users\\josephjo\\Documents\\Polarion2\\Polarion\\bundled\\updated\\EC_ALM_connector\\Commitdata.txt");
	
	}
	
	@Test 
	public void testLam() {
		
		
		
		Start s1 =()->{
			System.out.println("Hello");
		};
		s1.print();
	}
	
}

interface Start{
	public void print();
	
}
