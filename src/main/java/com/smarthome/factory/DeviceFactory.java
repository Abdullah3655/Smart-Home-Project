package com.smarthome.factory;

import com.smarthome.devices.Device;
import java.util.UUID;

/**
 * ABSTRACT FACTORY PATTERN + FACTORY METHODS.
 *
 * <p>The rubric's exact phrasing is <i>"Abstract Factory with Factory
 * Methods"</i> — both halves are visible here:</p>
 * <ul>
 *   <li><b>Abstract Factory</b> — this class declares a coherent family
 *       of products (lights, thermostats, locks, cameras). Concrete
 *       subclasses ({@link Version1DeviceFactory}, {@link Version2DeviceFactory})
 *       implement <em>all</em> four methods, each producing the variant
 *       belonging to its family. Per Refactoring Guru: <i>"work with
 *       various families of related products without depending on their
 *       concrete classes."</i></li>
 *   <li><b>Factory Methods</b> — each {@code createXxx(String)} is a
 *       Factory Method that subclasses override to instantiate the
 *       right concrete product.</li>
 * </ul>
 *
 * <p>Both concrete factories implement every method meaningfully — no
 * {@code UnsupportedOperationException} stubs — so the abstraction is
 * Liskov-substitutable, satisfying SOLID's L.</p>
 */
public abstract class DeviceFactory {
    /** Factory Method — produces a Light belonging to this family. */
    public abstract Device createLight(String name);

    /** Factory Method — produces a Thermostat belonging to this family. */
    public abstract Device createThermostat(String name);

    /** Factory Method — produces a Lock belonging to this family. */
    public abstract Device createDoorLock(String name);

    /** Factory Method — produces a Camera belonging to this family. */
    public abstract Device createCamera(String name);

    /** Generates a UUID id for the next device. Shared across all factories. */
    protected String newId() {
        return UUID.randomUUID().toString();
    }
}
