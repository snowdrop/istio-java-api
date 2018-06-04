package me.snowdrop.istio.api.model.v1.networking;

public enum ServiceEntryLocation {
    /**
     * Signifies that the service is external to the mesh.
     * Typically used to indicate external services consumed through APIs.
     */
    MESH_EXTERNAL(0),
    /**
     * Signifies that the service is part of the mesh.
     * Typically used to indicate services added explicitly
     * as part of expanding the service mesh
     * to include unmanaged infrastructure (e.g., VMs added to a Kubernetes based service mesh).
     */
    MESH_INTERNAL(1);

    private final int intValue;

    ServiceEntryLocation(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
