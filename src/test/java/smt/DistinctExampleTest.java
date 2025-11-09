package smt;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.junit.Assert;
import org.junit.Test;

public class DistinctExampleTest {
    @Test
    public void test() {
        Context ctx = new Context();
        IntExpr x = ctx.mkIntConst("x");
        IntExpr y = ctx.mkIntConst("y");
        // Build formula: 1 <= x <= 2
        BoolExpr p1 = ctx.mkAnd(ctx.mkLe(ctx.mkInt(1), x), ctx.mkLe(x, ctx.mkInt(2)));
        // Build formula: 1 <= y <= 2
        BoolExpr p2 = ctx.mkAnd(ctx.mkLe(ctx.mkInt(1), y), ctx.mkLe(y, ctx.mkInt(2)));
        // Build formula: distinct(x, y)
        BoolExpr p3 = ctx.mkDistinct(x, y);

        Solver solver = ctx.mkSolver();
        solver.add(p1, p2, p3);
        // Check satisfiability
        Status status = solver.check();
        Assert.assertEquals(Status.SATISFIABLE, status);
        // System.out.println(status);
        // Get a model
        Model model = solver.getModel();
        Assert.assertNotEquals(model.getConstInterp(x), model.getConstInterp(y));
        // System.out.println(model.getConstInterp(x));
        // System.out.println(model.getConstInterp(y));
    }
}
