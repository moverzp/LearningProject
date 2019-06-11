package com.moverzp.wenda;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WendaApplicationTests {

	@Test
	public void contextLoads() {
	}

	public static void main(String[] args) {
		Date date = new Date();
		System.out.println(date);
	}

}
