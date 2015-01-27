package controller;

import org.junit.Assert;
import org.junit.Test;

public class ControllerTest {
	@Test
	public void runGame() {
		Assert.assertEquals("Test", new Controller().runGame());
	}
}
