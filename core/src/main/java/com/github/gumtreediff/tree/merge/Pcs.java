/*
 * This file is part of GumTree.
 *
 * GumTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GumTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GumTree.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2016 Jean-Rémy Falleri <jr.falleri@gmail.com>
 * Copyright 2016 Floréal Morandat <florealm@gmail.com>
 */

package com.github.gumtreediff.tree.merge;

import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.utils.Couple;
import com.github.gumtreediff.utils.Pair;
import com.github.gumtreediff.tree.TreeContext;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Pcs {
    private ITree root;

    private ITree predecessor;

    private ITree successor;

    public ITree getRoot() {
        return root;
    }

    public ITree getPredecessor() {
        return predecessor;
    }

    public ITree getSuccessor() {
        return successor;
    }

    public Pcs(ITree root, ITree predecessor, ITree successor) {
        this.root = root;
        this.predecessor = predecessor;
        this.successor = successor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pcs pcs = (Pcs) o;

        if (root != null ? !root.equals(pcs.root) : pcs.root != null) return false;
        if (predecessor != null ? !predecessor.equals(pcs.predecessor) : pcs.predecessor != null) return false;
        return successor != null ? successor.equals(pcs.successor) : pcs.successor == null;
    }

    @Override
    public String toString() {
        return "(" + root + "," + predecessor + "," + successor + ")";
    }

    public String toPrettyString(TreeContext ctx) {
        return "(" + ctx.toPrettyString(root) + ","
                + ctx.toPrettyString(predecessor) + ","
                + ctx.toPrettyString(successor) + ")";
    }

    @Override
    public int hashCode() {
        int result = root != null ? root.hashCode() : 0;
        result = 31 * result + (predecessor != null ? predecessor.hashCode() : 0);
        result = 31 * result + (successor != null ? successor.hashCode() : 0);
        return result;
    }

    public static Set<Pcs> fromTree(ITree tree) {
        Set<Pcs> result = new HashSet<>();
        for (ITree t: tree.preOrder()) {
            int size = t.getChildren().size();
            for (int i = 0; i < size; i++) {
                ITree c = t.getChild(i);
                if (i == 0)
                    result.add(new Pcs(t, null, c));
                result.add(new Pcs(t, c, i == (size - 1) ? null : t.getChild(i + 1)));
            }
            if (size == 0)
                result.add(new Pcs(t, null, null));
        }
        result.add(new Pcs(null, tree, null));
        result.add(new Pcs(null, null, tree));
        return result;
    }

    public Pcs getOther(Set<Pcs> all) {
        for (Pcs pcs : all) {
            if (root == pcs.root && predecessor == pcs.predecessor && successor != pcs.successor)
                return pcs;
            if (root == pcs.root && predecessor != pcs.predecessor && successor == pcs.successor)
                return pcs;
            if (root != pcs.root && predecessor == pcs.predecessor && successor == pcs.successor)
                return pcs;
        }
        return null;
    }

    public Pcs getOtherSuccessor(Set<Pcs> all, Set<Pcs> ignored) {
        for (Pcs pcs : all) {
            if (ignored.contains(pcs))
                continue;
            if (root == pcs.root && predecessor == pcs.predecessor && successor != pcs.successor)
                return pcs;
        }
        return null;
    }

    public Pcs getOtherPredecessor(Set<Pcs> all, Set<Pcs> ignored) {
        for (Pcs pcs : all) {
            if (ignored.contains(pcs))
                continue;
            if (root == pcs.root && predecessor != pcs.predecessor && successor == pcs.successor)
                return pcs;
        }
        return null;
    }

    public Pcs getOtherRoot(Set<Pcs> all, Set<Pcs> ignored) {
        for (Pcs pcs : all) {
            if (ignored.contains(pcs))
                continue;
            if (root != pcs.root && predecessor == pcs.predecessor && successor == pcs.successor)
                if (predecessor != null && successor != null)
                    return pcs;
        }
        return null;
    }

    public static final String inspect(Set<Pcs> set, TreeContext context) {
        return set.stream().map(p -> p.toPrettyString(context)).collect(Collectors.joining(", "));
    }

    public static final String inspect(Pair<Pcs, Pcs> pair, TreeContext context) {
        return pair.inspect(p -> p.toPrettyString(context), p -> p.toPrettyString(context));
    }

    public static final String inspect(Couple<Pcs, Pcs> couple, TreeContext context) {
        return couple.inspect(p -> p.toPrettyString(context), p -> p.toPrettyString(context));
    }
}
