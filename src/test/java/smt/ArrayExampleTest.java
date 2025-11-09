package smt;

import com.microsoft.z3.ArrayExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.junit.Assert;
import org.junit.Test;

public class ArrayExampleTest {

    @Test
    public void test() {
        Context ctx = new Context();
        IntExpr x = ctx.mkIntConst("x");
        IntExpr y = ctx.mkIntConst("y");
        IntExpr z = ctx.mkIntConst("z");
        ArrayExpr a = ctx.mkArrayConst("a", ctx.getIntSort(), ctx.getIntSort());
        // Build formula: a[x] == y
        BoolExpr p1 = ctx.mkEq(ctx.mkSelect(a, x), y);
        // Build formula: a<x<|z>[x] == y
        BoolExpr p2 = ctx.mkEq(ctx.mkSelect(ctx.mkStore(a, x, z), x), y);

        Solver solver = ctx.mkSolver();
        solver.add(p1, p2);
        // Check satisfiability
        Status s1 = solver.check();
        Assert.assertEquals(Status.SATISFIABLE, s1);
        // System.out.println(s1);

        // Build formula: y != z
        BoolExpr p3 = ctx.mkNot(ctx.mkEq(y, z));
        solver.add(p3);
        // Check satisfiability
        Status s2 = solver.check();
        Assert.assertEquals(Status.UNSATISFIABLE, s2);
        // System.out.println(s2);
    }
}
