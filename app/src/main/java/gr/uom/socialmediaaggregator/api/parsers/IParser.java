package gr.uom.socialmediaaggregator.api.parsers;

import java.util.List;

import gr.uom.socialmediaaggregator.data.model.Post;

public interface IParser<T> {

    List<Post> parse(T data);

}
