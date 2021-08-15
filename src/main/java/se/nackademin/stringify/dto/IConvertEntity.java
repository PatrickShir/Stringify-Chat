package se.nackademin.stringify.dto;

/***
 * An interface for converting a data transfer object to an entity.
 */
public interface IConvertEntity<T> {

    T convertToEntity();
}
