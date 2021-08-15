package se.nackademin.stringify.domain;

/***
 * An interface for converting a model to a data transfer object.
 */
public interface IConvertDto<T> {

    T convertToDto();
}
