package com.tesis.yudith.showmethepast.view.navigation;

import java.util.Stack;

public class NavigationStack {
    private Stack<INavigationChild> stack;

    public NavigationStack() {
        this.stack = new Stack<>();
    }

    public void replaceChildren(INavigationChild children) {
        this.stack.clear();
        this.stack.add(children);
    }

    public void pushChildren(INavigationChild children) {
        this.stack.push(children);
    }

    public INavigationChild popChildren() {
        return this.stack.pop();
    }

    public INavigationChild peekChildren() {
        return this.stack.peek();
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    public boolean isLastOne() {
        return this.stack.size() == 1;
    }
}
