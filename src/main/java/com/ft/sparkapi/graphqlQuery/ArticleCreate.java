package com.ft.sparkapi.graphqlQuery;

import com.ft.sparkapi.model.Article;
import io.aexp.nodes.graphql.annotations.GraphQLArgument;
import io.aexp.nodes.graphql.annotations.GraphQLProperty;
import lombok.Data;

@Data
@GraphQLProperty(name = "articleCreate", arguments = { @GraphQLArgument(name= "record")})
public class ArticleCreate {
    private Article record;
}
