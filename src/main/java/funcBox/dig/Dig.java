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