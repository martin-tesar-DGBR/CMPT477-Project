package smt;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.junit.Assert;
import org.junit.Test;

public class IntegerExampleTest {
    @Test
    public void test() {
        // Create a new context
        Context ctx = new Context();
        // Create integer variables
        IntExpr x = ctx.mkIntConst("x");
        // Build formula: 1 <= x < 3
        BoolExpr p = ctx.mkAnd(ctx.mkLe(ctx.mkInt(1), x), ctx.mkLt(x, ctx.mkInt(3)));
        // Create a solver
        Solver solver = ctx.mkSolver();
        // Add formulas
        solver.add(p);
        // Check satisfiability
        Status status = solver.check();
        Assert.assertEquals(Status.SATISFIABLE, status);
        // System.out.println(status);
        // Get a model
        // Model model = solver.getModel();
        // System.out.println(model.getConstInterp(x));
    }
}
