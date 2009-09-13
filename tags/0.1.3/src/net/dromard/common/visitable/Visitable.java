package net.dromard.common.visitable;


/**
 * Visitable pattern interface.
 * <br>
 * @author Gabriel Dromard
 */
public interface Visitable {
    /**
     * Visit function.
     * @param visitor The visitor.
     * @throws Exception Any exception can occured during visit.
     */
	void accept(Visitor visitor) throws Exception;
}
