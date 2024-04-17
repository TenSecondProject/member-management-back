package org.colcum.admin.domain.user.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Embeddable
@EqualsAndHashCode
@ToString
public class Bookmark {

    private Long postId;

}

