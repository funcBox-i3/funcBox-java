package funcBox.dig;
/**
 * Factory entry point for the dig navigation API.
 *
 * <p>Parse once, then reuse the returned {@link DigContext} for repeated lookups.</p>
 *
 * @since 1.1.1
 */
public final class Dig {
    private Dig() {
    }
    /**
     * Creates a {@link DigContext} from raw JSON.
     *
     * <p>Parses the JSON string into an internal object graph <b>exactly once</b>.
     * Reuse the returned {@link DigContext} for all subsequent lookups on the same JSON.
     * Calling {@code Dig.of(json)} repeatedly in a loop is an <b>anti-pattern</b>
     * and is significantly slower than storing and reusing the context.</p>
     *
     * <p><b>Correct usage:</b></p>
     * <pre>
     * // Parse once
     * DigContext ctx = Dig.of(json);
     *
     * // Reuse many times
     * String name = ctx.getString("user.name");
     * int    age  = ctx.getInt("user.age");
     * </pre>
     *
     * @param json raw JSON text
     * @return immutable dig context; never null
     */
    public static DigContext of(String json) {
        return DigContext.fromJson(json);
    }
    /**
     * Creates a {@link DigContext} from a JSON string, map/list/array graph,
     * or an existing {@link DigContext}.
     *
     * @param source raw JSON or already materialized object graph
     * @return immutable dig context; never null
     */
    public static DigContext of(Object source) {
        return DigContext.fromSource(source);
    }
}