package main.symbolTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    public enum Kind { MODULE, STRUCT, FIELD, VARIABLE, PARAMETER, METHOD, CONSTRUCTOR }

    public static final class Symbol {
        private final Kind kind;
        private final String name;
        private final String type;
        private final boolean mutable;
        private final boolean isPrivate;
        private final int line;
        private final List<String> parameterTypes;
        private SymbolTable members;
        private boolean initialized;

        public Symbol(Kind kind, String name, String type, boolean mutable,
                      boolean isPrivate, int line) {
            this(kind, name, type, mutable, isPrivate, line, new ArrayList<String>());
        }

        public Symbol(Kind kind, String name, String type, boolean mutable,
                      boolean isPrivate, int line, List<String> parameterTypes) {
            this.kind = kind;
            this.name = name;
            this.type = type;
            this.mutable = mutable;
            this.isPrivate = isPrivate;
            this.line = line;
            this.parameterTypes = new ArrayList<>(parameterTypes);
        }

        public Kind getKind() { return kind; }
        public String getName() { return name; }
        public String getType() { return type; }
        public boolean isMutable() { return mutable; }
        public boolean isPrivate() { return isPrivate; }
        public int getLine() { return line; }
        public List<String> getParameterTypes() { return parameterTypes; }
        public SymbolTable getMembers() { return members; }
        public void setMembers(SymbolTable members) { this.members = members; }
        public boolean isInitialized() { return initialized; }
        public void setInitialized(boolean initialized) { this.initialized = initialized; }

        public String signatureKey() {
            return name + "(" + String.join(",", parameterTypes) + ")";
        }
    }

    private final SymbolTable parent;
    private final Map<String, Symbol> values = new LinkedHashMap<>();
    private final Map<String, Symbol> containers = new LinkedHashMap<>();
    private final Map<String, List<Symbol>> methods = new LinkedHashMap<>();

    public SymbolTable() { this(null); }
    public SymbolTable(SymbolTable parent) { this.parent = parent; }

    public SymbolTable getParent() { return parent; }

    public boolean defineValue(Symbol symbol) {
        if (values.containsKey(symbol.getName())) return false;
        values.put(symbol.getName(), symbol);
        return true;
    }

    public boolean defineContainer(Symbol symbol) {
        if (containers.containsKey(symbol.getName())) return false;
        containers.put(symbol.getName(), symbol);
        return true;
    }

    public boolean defineMethod(Symbol symbol) {
        List<Symbol> overloads = methods.computeIfAbsent(symbol.getName(), k -> new ArrayList<Symbol>());
        for (Symbol overload : overloads) {
            if (overload.signatureKey().equals(symbol.signatureKey())) return false;
        }
        overloads.add(symbol);
        return true;
    }

    public Symbol resolveValue(String name) {
        for (SymbolTable table = this; table != null; table = table.parent) {
            Symbol symbol = table.values.get(name);
            if (symbol != null) return symbol;
        }
        return null;
    }

    public Symbol resolveContainer(String name) {
        for (SymbolTable table = this; table != null; table = table.parent) {
            Symbol symbol = table.containers.get(name);
            if (symbol != null) return symbol;
        }
        return null;
    }

    public List<Symbol> resolveMethods(String name) {
        for (SymbolTable table = this; table != null; table = table.parent) {
            List<Symbol> symbols = table.methods.get(name);
            if (symbols != null) return symbols;
        }
        return new ArrayList<>();
    }

    public Symbol localValue(String name) { return values.get(name); }
    public Symbol localContainer(String name) { return containers.get(name); }
    public List<Symbol> localMethods(String name) {
        List<Symbol> symbols = methods.get(name);
        return symbols == null ? new ArrayList<Symbol>() : symbols;
    }
    public Collection<Symbol> localValues() { return values.values(); }
}
