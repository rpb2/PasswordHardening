package com.rpecebou.login;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rpecebou.init.InstructionTableGenerator;
import com.rpecebou.math.Polynomial;
import com.rpecebou.math.PseudoRandomGenerator;
import com.rpecebou.math.RandomPrimeGenerator;
import com.rpecebou.structures.Constants;
import com.rpecebou.structures.LoginAttempt;
import com.rpecebou.structures.NonVolatileStorageContent;
import com.rpecebou.structures.Point;

public class InstructionSelectorTest {
	private LoginAttempt _initialLogin;

	private BigInteger _hardenedPassword;

	private List<Point> _points;

	private NonVolatileStorageContent _content;

	private Polynomial _f;

	private BigInteger _q;

	@Test
	public void process() {
		_points = new InstructionSelector(_content).process(_initialLogin);
		for (Point p : _points) {
			Assert.assertEquals(_f.evaluate(p.getX()), p.getY());
		}
	}

	@Test
	public void process_usePointsToInterpolate() {
		_points = new InstructionSelector(_content).process(_initialLogin);
		System.out.println(_q.toString());
		BigInteger calculatedHardenedPassword = new HardenedPasswordCalculator(_points, _q).interpolate();
		Assert.assertEquals(_hardenedPassword, calculatedHardenedPassword);
	}

	@Before
	public void setUp() {
		_initialLogin = new LoginAttempt("Toto", Arrays.asList(new Integer[] { 10, 2, 20 }));
		_q = RandomPrimeGenerator.generate(Constants.Q_SIZE);
		BigInteger r = new PseudoRandomGenerator(Constants.Q_SIZE).next();
		_hardenedPassword = new PseudoRandomGenerator(Constants.Q_SIZE).next().mod(_q);
		_f = PseudoRandomGenerator.generatePolynomial(Constants.Q_SIZE, 2, _q, _hardenedPassword);
		_content = new InstructionTableGenerator(_q, r, _f, _initialLogin).process();
	}
}
