<?php

class Baz
{
    /** @var Foo */
    public $fooField;

    /** @var Foo */
    public $barField;

    public function getSibling(): Foo
    {
    }

    public function setSibling($sibling): void
    {

    }
}