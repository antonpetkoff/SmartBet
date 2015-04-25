package math.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestUtilities {

    private static final double EPSILON = 1e-14;
    
    @Test
    public void testFactorial() {
        assertEquals(Long.valueOf(6227020800L), Long.valueOf(MathModel.factorial(13)));
        assertEquals(Long.valueOf(87178291200L), Long.valueOf(MathModel.factorial(14)));
    }
    
    @Test
    public void testPoisson() {
        assertEquals(Double.valueOf(Math.pow(Math.E, -6)), Double.valueOf(MathModel.poisson(0, 6)));
        assertEquals(Double.valueOf(Math.pow(Math.E, -6) * 6.0), Double.valueOf(MathModel.poisson(1, 6)));
        assertEquals(Double.valueOf(Math.pow(Math.E, -6) * 6.0 * 6.0 / MathModel.factorial(2)), Double.valueOf(MathModel.poisson(2, 6)));
        assertEquals(Double.valueOf(Math.pow(Math.E, -6) * Math.pow(6, 3) / MathModel.factorial(3)), Double.valueOf(MathModel.poisson(3, 6)));
        assertEquals(Double.valueOf(Math.pow(Math.E, -6) * Math.pow(6, 4) / MathModel.factorial(4)), Double.valueOf(MathModel.poisson(4, 6)));   

        assertEquals(Double.valueOf(0.253123435905633), Double.valueOf(MathModel.poisson(1, 2.13)), EPSILON);
        assertEquals(Double.valueOf(0.238466676192325), Double.valueOf(MathModel.poisson(2, 1.37)), EPSILON);
    }
    
}
