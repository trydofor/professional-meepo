package pro.fessional.meepo.eval.fmt;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2021-01-04
 */
class CaseTest {
    private final Map<String, Object> ctx = Collections.emptyMap();

    @Test
    public void testCamelCase() {
        assertEquals("tryDoFor", Case.funCamelCase.eval(ctx, "try-do-for"));
        assertEquals("tryDoFor", Case.funCamelCase.eval(ctx, "tryDo-for"));
        assertEquals("tryDoFor", Case.funCamelCase.eval(ctx, "tryDO-FOR"));
        assertEquals("tryDoFor", Case.funCamelCase.eval(ctx, "tryDoFOR"));
        assertEquals("tryDoFor", Case.funCamelCase.eval(ctx, "TRyDoFOR"));
        assertEquals("tryDoFor", Case.funCamelCase.eval(ctx, "TryDoFOR"));
    }

    @Test
    public void testPascalCase() {
        assertEquals("TryDoFor", Case.funPascalCase.eval(ctx, "try-do-for"));
        assertEquals("TryDoFor", Case.funPascalCase.eval(ctx, "tryDo-for"));
        assertEquals("TryDoFor", Case.funPascalCase.eval(ctx, "tryDO-FOR"));
        assertEquals("TryDoFor", Case.funPascalCase.eval(ctx, "tryDoFOR"));
        assertEquals("TryDoFor", Case.funPascalCase.eval(ctx, "TRyDoFOR"));
        assertEquals("TryDoFor", Case.funPascalCase.eval(ctx, "TryDoFOR"));
    }

    @Test
    public void testSnakeCase() {
        assertEquals("try_do_for", Case.funSnakeCase.eval(ctx, "try-do-for"));
        assertEquals("try_do_for", Case.funSnakeCase.eval(ctx, "tryDo-for"));
        assertEquals("try_do_for", Case.funSnakeCase.eval(ctx, "tryDO-FOR"));
        assertEquals("try_do_for", Case.funSnakeCase.eval(ctx, "tryDoFOR"));
        assertEquals("try_do_for", Case.funSnakeCase.eval(ctx, "TRyDoFOR"));
        assertEquals("try_do_for", Case.funSnakeCase.eval(ctx, "TryDoFOR"));
    }

    @Test
    public void testBigSnake() {
        assertEquals("TRY_DO_FOR", Case.funBigSnake.eval(ctx, "try-do-for"));
        assertEquals("TRY_DO_FOR", Case.funBigSnake.eval(ctx, "tryDo-for"));
        assertEquals("TRY_DO_FOR", Case.funBigSnake.eval(ctx, "tryDO-FOR"));
        assertEquals("TRY_DO_FOR", Case.funBigSnake.eval(ctx, "tryDoFOR"));
        assertEquals("TRY_DO_FOR", Case.funBigSnake.eval(ctx, "TRyDoFOR"));
        assertEquals("TRY_DO_FOR", Case.funBigSnake.eval(ctx, "TryDoFOR"));
    }

    @Test
    public void testKebabCase() {
        assertEquals("try-do-for", Case.funKebabCase.eval(ctx, "try-do-for"));
        assertEquals("try-do-for", Case.funKebabCase.eval(ctx, "tryDo-for"));
        assertEquals("try-do-for", Case.funKebabCase.eval(ctx, "tryDO-FOR"));
        assertEquals("try-do-for", Case.funKebabCase.eval(ctx, "tryDoFOR"));
        assertEquals("try-do-for", Case.funKebabCase.eval(ctx, "TRyDoFOR"));
        assertEquals("try-do-for", Case.funKebabCase.eval(ctx, "TryDoFOR"));
    }

    @Test
    public void testBigKebab() {
        assertEquals("TRY-DO-FOR", Case.funBigKebab.eval(ctx, "try-do-for"));
        assertEquals("TRY-DO-FOR", Case.funBigKebab.eval(ctx, "tryDo-for"));
        assertEquals("TRY-DO-FOR", Case.funBigKebab.eval(ctx, "tryDO-FOR"));
        assertEquals("TRY-DO-FOR", Case.funBigKebab.eval(ctx, "tryDoFOR"));
        assertEquals("TRY-DO-FOR", Case.funBigKebab.eval(ctx, "TRyDoFOR"));
        assertEquals("TRY-DO-FOR", Case.funBigKebab.eval(ctx, "TryDoFOR"));
    }

    @Test
    public void testDotCase() {
        assertEquals("try.do.for", Case.funDotCase.eval(ctx, "try-do-for"));
        assertEquals("try.do.for", Case.funDotCase.eval(ctx, "tryDo-for"));
        assertEquals("try.do.for", Case.funDotCase.eval(ctx, "tryDO-FOR"));
        assertEquals("try.do.for", Case.funDotCase.eval(ctx, "tryDoFOR"));
        assertEquals("try.do.for", Case.funDotCase.eval(ctx, "TRyDoFOR"));
        assertEquals("try.do.for", Case.funDotCase.eval(ctx, "TryDoFOR"));
    }

    @Test
    public void testUpperCase() {
        assertEquals("TRY-DO-FOR", Case.funUpperCase.eval(ctx, "try-do-for"));
        assertEquals("TRYDO-FOR", Case.funUpperCase.eval(ctx, "tryDo-for"));
        assertEquals("TRYDO-FOR", Case.funUpperCase.eval(ctx, "tryDO-FOR"));
        assertEquals("TRYDOFOR", Case.funUpperCase.eval(ctx, "tryDoFOR"));
        assertEquals("TRYDOFOR", Case.funUpperCase.eval(ctx, "TRyDoFOR"));
        assertEquals("TRYDOFOR", Case.funUpperCase.eval(ctx, "TryDoFOR"));
    }

    @Test
    public void testLowerCase() {
        assertEquals("try-do-for", Case.funLowerCase.eval(ctx, "try-do-for"));
        assertEquals("trydo-for", Case.funLowerCase.eval(ctx, "tryDo-for"));
        assertEquals("trydo-for", Case.funLowerCase.eval(ctx, "tryDO-FOR"));
        assertEquals("trydofor", Case.funLowerCase.eval(ctx, "tryDoFOR"));
        assertEquals("trydofor", Case.funLowerCase.eval(ctx, "TRyDoFOR"));
        assertEquals("trydofor", Case.funLowerCase.eval(ctx, "TryDoFOR"));
    }

}