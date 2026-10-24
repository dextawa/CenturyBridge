package top.dext.centurybridge.step.v26_2.rt;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.graph.AbstractNetwork;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.Network;
import com.google.common.graph.ValueGraph;
import java.util.AbstractSet;
import java.util.Set;

/**
 * Guava 33.5 (26.1.2) -> 33.6 (26.2): Graph and ValueGraph newly demand asNetwork(). Guava's own graphs answer it from
 * AbstractBaseGraph: a live Network view whose edges are the graph's EndpointPairs, no parallel edges, unordered edges,
 * and in / out edge sets built like its package-private IncidentEdgeSet. A 33.5-era mod graph (implementing Graph or
 * ValueGraph itself, which 33.5 allowed without that method) gets the same view, built here from Guava 33.6's
 * AbstractBaseGraph$3 / IncidentEdgeSet bytecode over the public graph methods.
 */
public final class CbLibGraphNetwork<N> extends AbstractNetwork<N, EndpointPair<N>> {
    /** the graph methods the view reads: Graph's or ValueGraph's (their common BaseGraph is package-private) */
    private interface Base<N> {
        Set<N> nodes();

        Set<EndpointPair<N>> edges();

        boolean isDirected();

        boolean allowsSelfLoops();

        ElementOrder<N> nodeOrder();

        Set<N> adjacentNodes(N node);

        Set<N> predecessors(N node);

        Set<N> successors(N node);

        Set<EndpointPair<N>> incidentEdges(N node);

        Graph<N> asGraph();
    }

    private final Base<N> base;

    private CbLibGraphNetwork(Base<N> base) {
        this.base = base;
    }

    /** Graph.asNetwork() for a 33.5-era implementation */
    public static <N> Network<N, EndpointPair<N>> asNetwork(Graph<N> graph) {
        return new CbLibGraphNetwork<>(new Base<N>() {
            public Set<N> nodes() { return graph.nodes(); }
            public Set<EndpointPair<N>> edges() { return graph.edges(); }
            public boolean isDirected() { return graph.isDirected(); }
            public boolean allowsSelfLoops() { return graph.allowsSelfLoops(); }
            public ElementOrder<N> nodeOrder() { return graph.nodeOrder(); }
            public Set<N> adjacentNodes(N node) { return graph.adjacentNodes(node); }
            public Set<N> predecessors(N node) { return graph.predecessors(node); }
            public Set<N> successors(N node) { return graph.successors(node); }
            public Set<EndpointPair<N>> incidentEdges(N node) { return graph.incidentEdges(node); }
            public Graph<N> asGraph() { return graph; }
        });
    }

    /** ValueGraph.asNetwork() for a 33.5-era implementation */
    public static <N> Network<N, EndpointPair<N>> asNetwork(ValueGraph<N, ?> graph) {
        return new CbLibGraphNetwork<>(new Base<N>() {
            public Set<N> nodes() { return graph.nodes(); }
            public Set<EndpointPair<N>> edges() { return graph.edges(); }
            public boolean isDirected() { return graph.isDirected(); }
            public boolean allowsSelfLoops() { return graph.allowsSelfLoops(); }
            public ElementOrder<N> nodeOrder() { return graph.nodeOrder(); }
            public Set<N> adjacentNodes(N node) { return graph.adjacentNodes(node); }
            public Set<N> predecessors(N node) { return graph.predecessors(node); }
            public Set<N> successors(N node) { return graph.successors(node); }
            public Set<EndpointPair<N>> incidentEdges(N node) { return graph.incidentEdges(node); }
            public Graph<N> asGraph() { return graph.asGraph(); }
        });
    }

    @Override
    public Set<N> nodes() {
        return base.nodes();
    }

    @Override
    public Set<EndpointPair<N>> edges() {
        return base.edges();
    }

    @Override
    public Graph<N> asGraph() {
        return base.asGraph();
    }

    @Override
    public boolean isDirected() {
        return base.isDirected();
    }

    @Override
    public boolean allowsParallelEdges() {
        return false;
    }

    @Override
    public boolean allowsSelfLoops() {
        return base.allowsSelfLoops();
    }

    @Override
    public ElementOrder<N> nodeOrder() {
        return base.nodeOrder();
    }

    @Override
    public ElementOrder<EndpointPair<N>> edgeOrder() {
        return ElementOrder.unordered();
    }

    @Override
    public Set<N> adjacentNodes(N node) {
        return base.adjacentNodes(node);
    }

    @Override
    public Set<N> predecessors(N node) {
        return base.predecessors(node);
    }

    @Override
    public Set<N> successors(N node) {
        return base.successors(node);
    }

    @Override
    public Set<EndpointPair<N>> incidentEdges(N node) {
        return base.incidentEdges(node);
    }

    @Override
    public Set<EndpointPair<N>> inEdges(N node) {
        Preconditions.checkNotNull(node);
        Preconditions.checkArgument(nodes().contains(node));
        return nodeInvalidatableSet(new IncidentEdges(node, true), node);
    }

    @Override
    public Set<EndpointPair<N>> outEdges(N node) {
        Preconditions.checkNotNull(node);
        Preconditions.checkArgument(nodes().contains(node));
        return nodeInvalidatableSet(new IncidentEdges(node, false), node);
    }

    @Override
    public Set<EndpointPair<N>> adjacentEdges(EndpointPair<N> edge) {
        Preconditions.checkArgument(edges().contains(edge));
        N u = edge.nodeU();
        N v = edge.nodeV();
        return nodePairInvalidatableSet(Sets.difference(Sets.union(incidentEdges(u), incidentEdges(v)), ImmutableSet.of(edge)), u, v);
    }

    @Override
    public EndpointPair<N> incidentNodes(EndpointPair<N> edge) {
        Preconditions.checkArgument(edges().contains(edge));
        return edge;
    }

    /** Guava 33.6's IncidentEdgeSet over this view, INCOMING or OUTGOING */
    private final class IncidentEdges extends AbstractSet<EndpointPair<N>> {
        private final N node;
        private final boolean incoming;

        IncidentEdges(N node, boolean incoming) {
            this.node = node;
            this.incoming = incoming;
        }

        private Set<N> predecessorsOrEmpty(N n) {
            return incoming ? CbLibGraphNetwork.this.predecessors(n) : ImmutableSet.of();
        }

        private Set<N> successorsOrEmpty(N n) {
            return incoming ? ImmutableSet.of() : CbLibGraphNetwork.this.successors(n);
        }

        @Override
        public UnmodifiableIterator<EndpointPair<N>> iterator() {
            boolean directed = isDirected();
            if (incoming) {
                return Iterators.unmodifiableIterator(Iterators.transform(CbLibGraphNetwork.this.predecessors(node).iterator(),
                    p -> directed ? EndpointPair.ordered(p, node) : EndpointPair.unordered(p, node)));
            }
            return Iterators.unmodifiableIterator(Iterators.transform(CbLibGraphNetwork.this.successors(node).iterator(),
                s -> directed ? EndpointPair.ordered(node, s) : EndpointPair.unordered(node, s)));
        }

        @Override
        public int size() {
            return isDirected() ? predecessorsOrEmpty(node).size() + successorsOrEmpty(node).size() : adjacentNodes(node).size();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof EndpointPair<?> pair)) {
                return false;
            }
            if (isDirected() != pair.isOrdered()) {
                return false;
            }
            if (isDirected()) {
                Object source = pair.source();
                Object target = pair.target();
                return (node.equals(source) && successorsOrEmpty(node).contains(target))
                    || (node.equals(target) && predecessorsOrEmpty(node).contains(source));
            }
            Set<N> adjacent = adjacentNodes(node);
            Object u = pair.nodeU();
            Object v = pair.nodeV();
            return (node.equals(v) && adjacent.contains(u)) || (node.equals(u) && adjacent.contains(v));
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }
    }
}
