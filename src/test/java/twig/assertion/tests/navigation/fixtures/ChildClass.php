<?php

class ChildClass
{
    /** @var ChildClass */
    public $childObject;

    public function getText(): string
    {
        return "child";
    }

    public function getTarget(): TestTarget
    {
        return null;
    }

    public function getBam(): Bam
    {
        return null;
    }
}