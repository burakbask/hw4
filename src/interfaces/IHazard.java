package interfaces;

public interface IHazard extends ITerrainObject {
    // LightIceBlock : Yes , HoleInIce : No
    boolean isMovable();
}
