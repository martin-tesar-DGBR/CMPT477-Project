package smt;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import org.junit.Assert;
import org.junit.Test;

public class FunctionExampleTest {
    @Test
    public void test() {
        Context ctx = new Context();
        IntExpr a = ctx.mkIntConst("a");
        FuncDecl f = ctx.mkFuncDecl("f", new Sort[] {ctx.getIntSort()}, ctx.getIntSort());
        // Build formula: f(f(f(a))) == a
        BoolExpr p1 = ctx.mkEq(ctx.mkApp(f, ctx.mkApp(f, ctx.mkApp(f, a))), a);
        // Build formula: f(f(f(f(f(a))))) == a
        BoolExpr p2 = ctx.mkEq(ctx.mkApp(f, ctx.mkApp(f, ctx.mkApp(f, ctx.mkApp(f, ctx.mkApp(f, a))))), a);
        // Build formula: f(a) != a
        BoolExpr p3 = ctx.mkNot(ctx.mkEq(ctx.mkApp(f, a), a));

        Solver solver = ctx.mkSolver();
        solver.add(p1, p2, p3);
        // Check satisfiability
        Status status = solver.check();
        Assert.assertEquals(Status.UNSATISFIABLE, status);
        // System.out.println(status);
    }
}
