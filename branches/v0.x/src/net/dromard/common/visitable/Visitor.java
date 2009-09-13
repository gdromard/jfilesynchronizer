package net.dromard.common.visitable;

/**
 * Visitor pattern interface.
 * <br>
 * @author Gabriel Dromard
 */
public interface Visitor {
    /**
     * Visit function.
     * @param node The element object of the tree.
     * @throws Exception Any exception can occured during visit.
     */
    void visit(Visitable node) throws Exception;
}

