package smt;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.junit.Assert;
import org.junit.Test;

public class QuantifierExampleTest {

    public boolean isValid(Context ctx, BoolExpr formula) {
        Solver solver = ctx.mkSolver();
        BoolExpr negation = ctx.mkNot(formula);
        solver.add(negation);
        return solver.check() == Status.UNSATISFIABLE;
    }

    @Test
    public void testExmpleExists()  {
        Context ctx = new Context();
        IntExpr x = ctx.mkIntConst("x");
        IntExpr y = ctx.mkIntConst("y");
        // Build formula: x == 10
        BoolExpr p1 = ctx.mkEq(x, ctx.mkInt(10));
        // Build formula: exists y. x == y + y
        BoolExpr p2 = ctx.mkExists(new Expr[] {y}, ctx.mkEq(x, ctx.mkAdd(y, y)), 1, null, null, null, null);
        boolean valid = isValid(ctx, ctx.mkImplies(p1, p2));
        Assert.assertEquals(true, valid);
        // System.out.println(valid);
    }

    @Test
    public void testExampleForall() {
        Context ctx = new Context();
        IntExpr x = ctx.mkIntConst("x");
        IntExpr y = ctx.mkIntConst("y");
        IntExpr zero = ctx.mkInt(0);
        // Build formula: forall x, y. (x > 0 /\ y > 0 -> x + y > 0)
        BoolExpr body = ctx.mkImplies(
                ctx.mkAnd(ctx.mkGt(x, zero), ctx.mkGt(y, zero)),
                ctx.mkGt(ctx.mkAdd(x, y), zero));
        BoolExpr p = ctx.mkForall(new Expr[] {x, y}, body, 1, null, null, null, null);
        boolean valid = isValid(ctx, p);
        Assert.assertEquals(true, valid);
        // System.out.println(valid);
    }

}
