package com.dbottillo.mtgsearchfree.model.database;

import com.dbottillo.mtgsearchfree.model.IntParam;

import java.util.ArrayList;
import java.util.List;

class QueryComposer {

    private final static String LIKE_OPERATOR = "LIKE";

    static class Output {

        public String query;
        public List<String> selection;

        Output(String query, List<String> selection) {
            this.query = query;
            this.selection = selection;
        }
    }

    private StringBuilder stringBuilder;  //NOPMD
    private List<String> selection;

    QueryComposer(String initial) {
        stringBuilder = new StringBuilder(initial);
        selection = new ArrayList<>();
    }

    void addParam(String name, IntParam intParam) {
        if (intParam == null) {
            return;
        }
        addParam(name, intParam.getOperator(), intParam.getValue());
    }

    void addLikeParam(String name, String value) {
        addParam(name, LIKE_OPERATOR, value);
    }

    void addParam(String name, String operator, int value) {
        if (isValid(name, operator, String.valueOf(value))) {
            checkFirstParam();
            stringBuilder.append("(CAST(");
            stringBuilder.append(name);
            stringBuilder.append(" as integer) ");
            stringBuilder.append(operator);
            stringBuilder.append(" ? AND ");
            stringBuilder.append(name);
            stringBuilder.append(" != '')");
            addSelection(operator, String.valueOf(value));
        }
    }

    void addParam(String name, String operator, String value) {
        if (isValid(name, operator, value)) {
            checkFirstParam();
            addOneParam(name, operator);
            addSelection(operator, value);
        }
    }

    void addMultipleParam(String name, String operator, String paramOperator, String... values) {
        if (name == null || name.length() <= 0 || values.length == 0) {
            return;
        }
        checkFirstParam();
        stringBuilder.append("(");
        for (int i = 0; i < values.length; i++) {
            addOneParam(name, operator);
            addSelection(operator, values[i]);
            if (i < values.length - 1) {
                stringBuilder.append(" ");
                stringBuilder.append(paramOperator);
                stringBuilder.append(" ");
            }
        }
        stringBuilder.append(")");
    }

    public void append(String path) {
        stringBuilder.append(" ");
        stringBuilder.append(path);
    }

    private void addSelection(String operator, String value) {
        if (operator.equalsIgnoreCase(LIKE_OPERATOR)) {
            selection.add("%" + value + "%");
        } else {
            selection.add(value);
        }
    }

    private void checkFirstParam() {
        if (!selection.isEmpty()) {
            stringBuilder.append(" AND ");
        } else {
            stringBuilder.append(" WHERE ");
        }
    }

    private void addOneParam(String name, String operator) {
        stringBuilder.append(name)
                .append(" ")
                .append(operator).append(" ?");
    }

    private boolean isValid(String name, String operator, String value) {
        if (name == null || name.length() <= 0
                || operator == null || operator.length() <= 0
                || value.length() <= 0) {
            return false;
        }
        return true;
    }

    public Output build() {
        return new Output(stringBuilder.toString(), selection);
    }
}
